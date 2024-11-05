# Step 1: Use the official Node.js image as the base image
FROM node:18-alpine

# Step 2: Set the working directory inside the container
WORKDIR /home/associate/Documents/capstone-fse1-group4/midtier-trade-marshals

# Step 3: Copy package.json and package-lock.json (or yarn.lock) to the container
COPY package*.json ./

# Step 4: Install dependencies inside the container
RUN npm install

# Step 5: Copy the rest of the application code into the container
COPY . .

# Step 6: Expose the port that the app will run on (usually 3000 for Node.js)
EXPOSE 4000

# Step 7: Define the command to run your app (assuming the start script is defined in package.json)
CMD ["npm", "run", "dev"]