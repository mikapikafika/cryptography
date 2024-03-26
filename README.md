<h1 align="center" id="title">Cryptography Project</h1>

<p id="description">The goal of this project was to implement the AES encryption algorithm and DSA (Digital Signature Algorithm) in an application with a user-friendly graphical interface.</p>

  
  
<h2>🧐 Features</h2>

Here're some of the project's best features:

*   generating or loading a cipher key from file
*   encoding a file using the AES algorithm
*   decoding a file
*   signing a file with DSA and verifying the signature

<h2>🛠️ Installation Steps:</h2>

<p>1. Ensure you have Java installed (this project uses Java 17).</p>

<p>2. To build the project you can use the provided Maven wrapper scripts:</p>
<p>On Unix-based systems:</p>

```
./mvnw clean install mvnw.cmd clean install
```
<p>On Windows:</p>

```
mvnw.cmd clean install
```

<p>3. To run the program navigate to the View module directory in your terminal and type:</p>
<p>On Unix-based systems:</p>

```
./mvnw clean javafx:run  // mvnw.cmd clean javafx:run
```
<p>On Windows:</p>

```
mvnw.cmd clean javafx:run
```
