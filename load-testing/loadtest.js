import http from 'k6/http';
import { check, sleep } from 'k6';
import { Trend } from 'k6/metrics';

const matchLatency = new Trend('driver_match_latency_ms');

const BASE_LOCATION = 'http://localhost:8082/api/v1/locations/drivers';
const BASE_RIDE = 'http://localhost:8083/api/v1/rides';

// center point drivers/riders cluster around
const CENTER_LAT = 18.52;
const CENTER_LNG = 73.85;
const SPREAD = 0.02; // ~2km radius roughly

function jitter(center) {
  return center + (Math.random() - 0.5) * SPREAD;
}

export const options = {
  scenarios: {
    driver_pings: {
      executor: 'constant-vus',
      vus: 200,           // 200 concurrent "drivers"
      duration: '2m',
      exec: 'driverPing',
    },
    ride_requests: {
      executor: 'ramping-vus',
      startVUs: 0,
      stages: [
        { duration: '20s', target: 10 },
        { duration: '1m', target: 50 },
        { duration: '20s', target: 0 },
      ],
      exec: 'requestRide',
      startTime: '10s', // let drivers populate Redis first
    },
  },
};

// --- Scenario 1: simulate drivers pinging location every few seconds ---
export function driverPing() {
  const driverId = `driver-${__VU}`;
  const payload = JSON.stringify({
    driver_id: driverId,
    latitude: jitter(CENTER_LAT),
    longitude: jitter(CENTER_LNG),
  });

  const res = http.post(`${BASE_LOCATION}/update`, payload, {
    headers: { 'Content-Type': 'application/json' },
  });

  check(res, { 'location update 200': (r) => r.status === 200 });
  sleep(3 + Math.random() * 2); // real drivers ping every ~3-5s
}

// --- Scenario 2: request a ride, then poll until matched ---
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
    return; // couldn't parse, skip polling
  }
  if (!rideId) return;

  // poll until matched or timeout (max 10s)
  let matched = false;
  for (let i = 0; i < 20; i++) {
    sleep(0.5);
    const check_res = http.get(`${BASE_RIDE}/${rideId}`);
    if (check_res.status === 200) {
      const body = JSON.parse(check_res.body);
      // adjust field/value to match your actual status enum
      if (body.status === 'MATCHED' || body.status === 'ACCEPTED' || body.driver_id) {
        matchLatency.add(Date.now() - startTime);
        matched = true;
        break;
      }
    }
  }

  check(null, { 'driver matched within 10s': () => matched });
}