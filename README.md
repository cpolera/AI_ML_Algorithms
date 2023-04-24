# AI and ML Implementations

Java 17 / Maven Project

This was a playground to try out implementing different AI/ML paradigms early in my coding career.  

The Feedforward Neural Network was expanded later for use with my Master's Capstone project which I hope to publish in the near future.

Update Notes:

April 3, 2023: I am currently revisiting the project to resolve some of those issues. This includes bug fixes, applying better coding standards, and most importantly leveraging better DSA implementations for performance and maintainability

April 21, 2023: I've refactored quite a bit. Most models and algorithms are now readable as well as less complex in their logic. I've also fixed several bugs along the way and abstracted where necessary. It was both pleasant to see how far I've come since writing this code originally whilst also being groan-inducing to see what I had coded those years ago. 
## Models
Model implementations can be found at src > main > java > models
Documentation on how to run these is forthcoming. 

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

### Particle Swarm Optimization
WIP - Needs implementation

## Development
Requires Java 8 and Maven. Developed with IntelliJ primarily. 

If adding a dependency while using IntelliJ, add dependency to pom file, right click the file, go to Maven, then Reload Project 

## Environment Variables

Utilizing a .env file temporarily to set certain flags until a config structure is built

Add a .env file to project root and set the following:
```
# 1-5 with 5 being most granular
FFN_DEBUG_LEVEL=1 

FFN_END_TRAINING_CYCLE_EARLY=1

FFN_END_TRAINING_OVERALL_EARLY=1

QAP_DEBUG_LEVEL=1
```

Use `System.getProperty("{PROPERTY NAME}")` to get the value
