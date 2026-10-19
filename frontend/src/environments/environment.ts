export const environment = {
  production: false,
  // Dev: requests go through the Angular proxy (proxy.conf.json) to Spring Boot on :8080.
  // Prod: set to your deployed API origin, e.g. 'https://api.example.com'.
  apiBase: '/api',
};
