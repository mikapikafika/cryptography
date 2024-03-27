<h1 align="center" id="title">AES Encryptor with DSA</h1>

<p id="description">The goal of this Java-based project was to implement the AES encryption algorithm and DSA (Digital Signature Algorithm) in an application with a user-friendly graphical interface. Made for the Foundations of Cryptography course at TUL.</p>

  
  
<h2>🧐 Features</h2>

Here're some of the project's best features:

*   Generating, reading from and saving cryptographic keys to file
*   Encoding a file using the AES algorithm. It supports key lengths of 128, 192, and 256 bits
*   Decoding a file
*   Signing a file with DSA and verifying the signature

<h2>💻 Built with</h2>

Technologies used in the project:

* Java
* JavaFX
* Maven

<h2>🧑‍💻 Code structure</h2> 

The project is divided into two main modules:

* Model: contains the implementation of the AES and DSA algorithms
* View: is responsible for the user interface of the application

<h2>🛠️ Installation steps:</h2>

Make sure you have all the dependencies needed installed (Java 17 & JavaFX).

<p>To build and run this project, use the following command in the terminal:</p>

```
mvnw clean install 
```

<p>3. To run the GUI part of the project, type:</p>

```
mvnw clean javafx:run
```
