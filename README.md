# RoboSim (Robot Simulator)

Java based portable simulator to visualize and understand the *Robot Localization*, *Path planning*, *Path Smoothing* and *PID controller* concepts.

![alt text](https://a.fsdn.com/con/app/proj/r-localization/screenshots/5.Path%20Smoother.png/1 "Logo Title Text 1")

## Blog : robosimblog.wordpress.com
## Download : https://sourceforge.net/projects/r-localization/

### Modules
* Filters
  * Histogram Filters
  * Kalman Filters
  * Particles Filters
* Path Planning
* Path Smoothing
* PID Controller

### Path Planning Algorithms

* BFS
* DFS
* A Star
* Dynamic Programming

### Heuristics

* Euclidean Distance
* Euclidean Distance(+)
* Euclidean Distance(*)
* Euclidean Distance Squared
* Manhattan Distance
* Chebyshev Distance

SYSTEM REQUIREMENTS
-------------------

RoboSim needs a JRE of at least version 1.6 ([Java SE 6.0](http://www.oracle.com/technetwork/java/javase/downloads/index.html)). If you want to build the jar from source, you will
also need [Maven](http://maven.apache.org/).

INSTALLATION FROM SOURCE
------------------------

	git clone https://github.com/habsoft/robosim.git
	cd robosim
	mvn install
	
After the build is complete, the jar will then be built as target/RoboSim/RoboSim.jar.

Suggestions, Feedback
----
This project is still under development, and we are working on many new features and controls. Your suggestions and feedback is required to improve this **Robot Simulator**.
Please email [Faisal Hameed](mailto:faisal.hameed.pk@gmail.com) with any feedback.

Background and Contributors
---------------------------
Inspired from an online course [Artificial Intelligence for Robotics] (https://www.udacity.com/course/artificial-intelligence-for-robotics--cs373) taught by [Sebastian Thrun](https://en.wikipedia.org/wiki/Sebastian_Thrun).
This **Robot Simulator** was originally started by [Faisal Hameed](https://pk.linkedin.com/in/faisalhameedpk
) in April 2012 and its first version [1.0.1] (https://sourceforge.net/projects/r-localization/files/RoboSim%20ver%201.0.1.rar/download) was release in July 2012.
