# TSOpen

TSOpen is a tool to detect logic bombs in Android applicatons. This is an open implementation of TriggerScope made thanks to the details given in the 2016 Security and Privacy paper by Fratantonio & al.

## Getting Started

### Downloading the tool

I do not provide any pre-built JAR as a release yet. Therefore one has to do the following to get the tool : 

<pre>
git clone https://github.com/dusby/TSOpen.git
</pre>

### Installing the tool

To install the tool, one just has to go into cloned repository and run mvn install command as follow :

<pre>
cd TSOpen
mvn install
</pre>

The built JAR will be in "target" folder with the following name : 
* TSOpen-X.Y-jar-with-dependencies.jar
Where X.Y is the current version of the tool.

### Using the tool

To run the tool, simply issue this command : 

<pre>
java -jar TSOpen/target/TSOpen-X.Y-jar-with-dependencies.jar <i>options</i>
</pre>

There are multiples options to run the tool, use --help to see all of them.

Two of them are currently required : 

<pre>
java -jar TSOpen/target/TSOpen-X.Y-jar-with-dependencies.jar -f \<APK file> -p \<path/to/android/platforms>
</pre>

Indeed, one has to provide a file to analyze and the path to the android platforms folder (in Android SDL folder).

## Built With

* [Maven](https://maven.apache.org/) - Dependency Management

## Authors

* **Jordan Samhi** - [Dusby](https://github.com/dusby)

## Publication

If one wants to know more about the implementation details please check the [related research paper](https://seclab.ccs.neu.edu/static/publications/sp2016triggerscope.pdf).

## License

This project is licensed under the GPLv3 License - see the [LICENSE.md](LICENSE.md) file for details
