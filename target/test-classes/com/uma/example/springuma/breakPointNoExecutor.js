// Grupo M: Alejandro López Ortega y Pablo Galvez Castillo

// import necessary module
import http from "k6/http";
import { check } from "k6";

export const options = { // punto de ruptura
    stages: [
        { duration: '2m', target: 10000 },
        { duration: '2m', target: 30000 },
        { duration: '2m', target: 50000 },
        { duration: '2m', target: 75000 },
        { duration: '2m', target: 100000 },
    ],
    thresholds: {
        http_req_failed: [{
            threshold: 'rate<=0.01',
            abortOnFail: true,
        }]
    }
};  

// ejecutar  k6 run --out web-dashboard=export=report-test.html breakPointNoExecutor.js
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