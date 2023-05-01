# AI and ML Implementations

Java 17 / Maven Project

This was a playground to try out implementing different AI/ML paradigms early in my coding career.  

The Feedforward Neural Network was expanded later for use with my Master's Capstone project which I hope to publish in the near future.

Update Notes:

April 3, 2023: I am currently revisiting the project to resolve some of those issues. This includes bug fixes, applying better coding standards, and most importantly leveraging better DSA implementations for performance and maintainability

April 21, 2023: I've refactored quite a bit. Most models and algorithms are now readable as well as less complex in their logic. I've also fixed several bugs along the way and abstracted where necessary. It was both pleasant to see how far I've come since writing this code originally whilst also being groan-inducing to see what I had coded those years ago. 

April 25, 2023: Add Spring Boot and started developing APIs
## Models
Model implementations can be found at src > main > java > com > implementations > models

### Bayesian Classifier
Refactored Apr 2023

### Feedforward Neural Network
Refactored Apr 2023

### Markov Model
Needs code review and refactoring

### K-Nearest Neighbors Algorithm
Refactored Apr 2023

### Quadratic Assignment Problem
Refactored Apr 2023
Started frontend Apr 2023

### Particle Swarm Optimization
WIP - Needs implementation

## Running project
This project is still being developed

However you can now start the application with Spring Boot as begin to access models.
To start the application:
` $ mvn clean spring-boot:run `

Only the QAP model is ready at this time with limited functionality.

Create (only the provided file here can be used for now):
` $ curl -X POST localhost:9000/api/qap/create -H 'Content-type:application/json' -d '{"filename": "src/main/resources/qap4.txt"}' `

Get:
` $ curl -X GET localhost:9000/api/qap/{ID} `
or
` $ curl -X GET localhost:9000/api/qap `

Solve QAP:
` $ curl -X GET localhost:9000/api/qap/solve/{ID} `

To spin up the UI, you'll need to cd into client, and run
` $ npm run serve `

## Development
Requires Java 17 and Maven. Developed with IntelliJ primarily. 

## Environment Variables

Utilizing a .env file temporarily to set certain flags until a config structure is built for the backend

Add a .env file to project root and set the following:
```
# 1-5 with 5 being most granular
FFN_DEBUG_LEVEL=1 

FFN_END_TRAINING_CYCLE_EARLY=1

FFN_END_TRAINING_OVERALL_EARLY=1

QAP_DEBUG_LEVEL=1
```

Use `System.getProperty("{PROPERTY NAME}")` to get the value
