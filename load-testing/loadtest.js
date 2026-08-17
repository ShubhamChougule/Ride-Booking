import http from 'k6/http';
import { check, sleep } from 'k6';
import { Trend, Counter } from 'k6/metrics';

const matchLatency = new Trend('driver_match_latency_ms');
const driversSeeded = new Counter('drivers_seeded');

const BASE_LOCATION = 'http://localhost:8082/api/v1/locations/drivers';
const BASE_RIDE = 'http://localhost:8083/api/v1/rides';

// center point drivers/riders cluster around
const CENTER_LAT = 39.52;
const CENTER_LNG = 28.85;

// SPREAD in degrees, converted from a real radius so it matches your app's GEOSEARCH radius
const RADIUS_KM = 5;
const SPREAD = RADIUS_KM / 111; // ~1 degree lat ≈ 111km

function jitter(center) {
  return center + (Math.random() - 0.5) * 2 * SPREAD;
}

export const options = {
  scenarios: {
    // ── Phase 1: bulk-seed 10,000 static drivers into Redis ──
    seed_drivers: {
      executor: 'shared-iterations',
      vus: 50,               // 50 workers splitting the 10k inserts
      iterations: 10000,     // exactly 10,000 drivers, no more no less
      maxDuration: '60s',
      exec: 'seedDriver',
      startTime: '0s',
    },

    // ── Phase 2: a subset of drivers actively ping location (realistic — not all 10k ping simultaneously) ──
    driver_pings: {
      executor: 'constant-vus',
      vus: 100,
      duration: '2m',
      exec: 'driverPing',
      startTime: '45s',      // starts after seeding is expected to finish
    },

    // ── Phase 3: 20 concurrent riders requesting rides ──
    ride_requests: {
      executor: 'constant-vus',
      vus: 20,
      duration: '1m30s',
      exec: 'requestRide',
      startTime: '60s',      // starts after seeding + a short buffer for drivers to be queryable
    },
  },
};

// --- Phase 1: seed 10,000 static drivers (one-time bulk insert) ---
export function seedDriver() {
  const driverId = `driver-seed-${__VU}-${__ITER}`;
  const payload = JSON.stringify({
    driver_id: driverId,
    latitude: jitter(CENTER_LAT),
    longitude: jitter(CENTER_LNG),
  });

  const res = http.post(`${BASE_LOCATION}/update`, payload, {
    headers: { 'Content-Type': 'application/json' },
  });

  check(res, { 'driver seeded 200': (r) => r.status === 200 });
  driversSeeded.add(1);
}

// --- Phase 2: simulate a subset of drivers actively pinging location ---
export function driverPing() {
  const driverId = `driver-live-${__VU}`; // separate namespace from seeded drivers
  const payload = JSON.stringify({
    driver_id: driverId,
    latitude: jitter(CENTER_LAT),
    longitude: jitter(CENTER_LNG),
  });

  const res = http.post(`${BASE_LOCATION}/update`, payload, {
    headers: { 'Content-Type': 'application/json' },
  });

  check(res, { 'location update 200': (r) => r.status === 200 });
  sleep(3 + Math.random() * 2);
}

// --- Phase 3: request a ride, then poll until matched ---
export function requestRide() {
  const riderId = `rider-${__VU}-${Date.now()}`;
  const payload = JSON.stringify({
    rider_id: riderId,
    pickup_latitude: jitter(CENTER_LAT),
    pickup_longitude: jitter(CENTER_LNG),
    pick_up_address: 'Pune, Maharashtra, India',
    drop_latitude: 19.0760,
    drop_longitude: 72.8777,
    drop_address: 'Mumbai, Maharashtra, India',
  });

  const startTime = Date.now();
  const res = http.post(`${BASE_RIDE}/request`, payload, {
    headers: { 'Content-Type': 'application/json' },
  });

  check(res, { 'ride request 200/201': (r) => r.status === 200 || r.status === 201 });

  let rideId;
  try {
    rideId = JSON.parse(res.body).ride_id || JSON.parse(res.body).id;
  } catch (e) {
    return;
  }
  if (!rideId) return;

  let matched = false;
  for (let i = 0; i < 20; i++) {
    sleep(0.5);
    const check_res = http.get(`${BASE_RIDE}/${rideId}`);
    if (check_res.status === 200) {
      const body = JSON.parse(check_res.body);
      if (body.status === 'MATCHED' || body.status === 'ACCEPTED' || body.driver_id) {
        matchLatency.add(Date.now() - startTime);
        matched = true;
        break;
      }
    }
  }

  check(null, { 'driver matched within 10s': () => matched });
}