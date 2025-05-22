// import necessary module
import http from "k6/http";
import { check } from "k6";

  export const options = { // punto de ruptura
    scenarios: {
    breakpoint: {
    executor: 'ramping-arrival-rate', // Incrementa la carga exponencial
    preAllocatedVUs: 1000, //VUs alocados inicialmente
    maxVUs: 1e7, //VUs maximo
    stages: [
    { duration: '10m', target: 100000 }, // just slowly ramp-up to a HUGE load
    ]
    }
    },
};  

// ejecutar  k6 run --out web-dashboard=export=report-test.html breakPoint.js
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