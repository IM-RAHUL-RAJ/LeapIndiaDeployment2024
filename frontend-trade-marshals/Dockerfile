FROM node:14 AS build
 
# Set the working directory
WORKDIR /home/associate/Documents/capstone-fse1-group4/frontend-trade-marshals
 
# Copy package.json and package-lock.json
COPY package*.json ./
 
# Install dependencies
RUN npm install
 
# Install Angular CLI and specific versions of packages
RUN npm install -g @angular/cli@12
#RUN npm install @angular-devkit/build-angular@13.1.13 --force
 
# Copy the rest of the application code
COPY . .
 
# Expose the port the app runs on
EXPOSE 4200
 
# Serve the application
CMD ["ng", "serve", "--host", "0.0.0.0"]