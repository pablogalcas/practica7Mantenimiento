// Grupo M: Alejandro López Ortega y Pablo Galvez Castillo

// import necessary module
import http from "k6/http";
import { check } from "k6";

export const options = { // smoke
  vus: 5, // Clave para la prueba de humo. Manténgalo en 2, 3, máximo 5 Vus
  duration: '1m', // Esto puede ser más corto o sólo unas pocas iteraciones
  thresholds: {
    http_req_failed: [{
        threshold: 'rate<0.01',
        abortOnFail: true,
    }],
    http_req_duration: [{
      threshold: 'avg<=100'
    }],
  }
  };

// ejecutar  k6 run --out web-dashboard=export=report-test.html smokeTest.js
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