# AI and ML Implementations

This was a playground to try out implementing different AI/ML paradigms early in my coding career. Some of the solutions are not ideal or performant. 

The Feed-forward Neural Network was expanded later for use with my Master's Capstone project

April 3, 2023: I am currently revisiting the project to resolve some of those issues. This includes bug fixes, applying better coding standards, and most importantly leveraging better DSA implementations for performance and maintainability


### Development

If adding a dependency while using IntelliJ, add dependency to pom file, right click the file, go to Maven, then Reload Project 

### Environment Variables
Add a .env file to project root and set the following:
```
# 1-5 with 5 being most granular
FFN_DEBUG_LEVEL=1 
```

Use `System.getProperty("{PROPERTY NAME}")` to get the value
