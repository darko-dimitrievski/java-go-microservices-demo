FROM golang:1.22-alpine AS build
WORKDIR /app
COPY go.mod .
COPY main.go .
RUN go build -o notification-service .

FROM alpine:3.20
WORKDIR /app
COPY --from=build /app/notification-service .
EXPOSE 8081
ENTRYPOINT ["./notification-service"]