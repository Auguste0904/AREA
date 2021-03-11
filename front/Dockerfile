FROM node:latest
RUN mkdir -p /app
WORKDIR /app
COPY package.json /app
RUN npm install
COPY . /app
RUN ln -s /mobile_app/debug/app-debug.apk public/client.apk
EXPOSE 8081
CMD [ "npm", "run", "serve" ]