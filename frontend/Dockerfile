# Build Stage
FROM node:18-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

# Production Stage
FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
# Copy config as a template; PORT and BACKEND_URL are substituted at container startup
COPY nginx.prod.conf /etc/nginx/nginx.prod.conf.template
EXPOSE 80
# Only substitute ${PORT} and ${BACKEND_URL} to avoid corrupting nginx's own $-variables
CMD ["/bin/sh", "-c", "envsubst '${PORT} ${BACKEND_URL}' < /etc/nginx/nginx.prod.conf.template > /etc/nginx/conf.d/default.conf && nginx -g 'daemon off;'"]
