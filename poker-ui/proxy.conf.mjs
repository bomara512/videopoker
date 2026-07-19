// Forwards API calls from ng serve to the backend. SERVER_PORT is the same
// variable Spring Boot reads, so one value moves both sides together.
const port = process.env.SERVER_PORT ?? 8080;

export default [
  {
    context: ['/game'],
    target: `http://localhost:${port}`,
  },
];
