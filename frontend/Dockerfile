# Build stage
FROM node:18-alpine AS build
WORKDIR /app

# Copy package files
COPY package*.json ./

# Install dependencies
RUN npm ci

# Copy source code
COPY . .

# Build the Angular app
RUN npm run build

# Production stage - using Node.js with serve
FROM node:18-alpine
WORKDIR /app

# Install serve globally
RUN npm install -g serve

# Copy the built app from build stage (Angular 17+ puts files in browser subdirectory)
COPY --from=build /app/dist/basis-frontend/browser /app

# Expose port
EXPOSE 80

# Start serve on port 80
CMD ["serve", "-s", "/app", "-l", "80"]