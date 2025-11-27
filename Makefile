.PHONY: build run test clean stop help

help:
	@echo "Available commands:"
	@echo "  make build   - Build the project"
	@echo "  make run     - Start with Docker Compose"
	@echo "  make test    - Run tests"
	@echo "  make clean   - Stop and clean everything"
	@echo "  make stop    - Stop containers"

build:
	mvn clean package

run:
	docker-compose up --build

test:
	mvn test

clean:
	docker-compose down -v
	mvn clean

stop:
	docker-compose down
