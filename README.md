BGU Course Systems Programming, Assignment 2 Created by Tom and Nave Grade: 100

Written in Java

The assignment:

Create a program to immitate usage of a centralized processing pool:

A University has servers that can process data and train machine learning models (theoretically), and users (students) can request to train and test their models using the university's resources.

This assignment tested our skills in Java generics, concurrency and synchronization.
We've worked with the concepts of Micro Services, Promise based programming with Futures, Design by Contract and Test Driven Development.


Out implementation:

The program parses an input file consisting of the different objects (Micro-services) we're to create (Students, Models, University resources such as CPUs and GPUs, and so on) and uses threads to operate the student's requests to train\test their models, which are eventually published to conferences. 
Since the program is timed, at the end of the timer it generates an output file which details every trained model, the total time (in ticks) which all the GPUs\CPUs worked and the amount of data batches processed.
