//31568

// import necessary module
import http from "k6/http";
import { check } from "k6";

const max = 15283;

export const options = { // average load
    stages: [
    { duration: '3m', target: Math.floor(max*0.5)},
    { duration: '3m', target: Math.floor(max*0.5)},
    { duration: '2m', target: 0},
    ],
    thresholds: {
        http_req_failed: [{
            threshold: 'rate<=0.01',
            abortOnFail: true,
        }],
        http_req_duration: [{
            threshold: 'avg<=1000'
        }],
    }
};

// ejecutar  k6 run --out web-dashboard=export=report-test.html spikeTest.js
export default function () {

    // define URL and payload
    const url = "http://localhost:8080/medico/1";

    // send a post request and save response as a variable
    const res = http.get(url);

    // Log the request body
    //console.log(res.body);

    // check that response is 200
    check(res, {
        "response code was 200": (res) => res.status == 200,
    });
    //sleep(1);
}