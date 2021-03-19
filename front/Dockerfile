FROM node:latest
RUN mkdir -p /app
WORKDIR /app
COPY package.json /app
RUN npm install
COPY . /app
EXPOSE 8081
CMD ["sh", "-c", "ln -sf /mobile_app/client.apk public/client.apk && npm run serve"]