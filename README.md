# Fingerprint Scanner Robot

Welcome to the Fingerprint Scanner Robot project! This project is comprised of two robots to test fingerprint scanners. The first robot is the Networked Automated Fingerprint Scanner Test Robot (NAFSTR), and the second is the Networked Automated Rolled Fingerprint Scanner Test Robot (NARFSTR). For more information, check out the documentation  at `Documentation/build/html/index.html` or `Documentation/build/latex/FingerprintScannerRobot.pdf`.

For a video of the NAFSTR, see: https://www.youtube.com/watch?v=lXbDDAqTphQ

## Project Contents:

* Circuit: contains all the files related to the robots' circuitry.
* Code: contains all the code
    * CSSharpLibrary: contains a Visual Studio C# solution with the CSharpLibrary for controlling the NAFSTR.
    * FingerprintRobotControl: contains an Eclipse Java project for controlling the robot.
    * FingerprintRobotLocalControl: contains all the C++ code that runs on the robots.
        * lib: libraries the code needs.
        * MACUploader: contains a program for uploading MAC addresses to the robots.
        * NAFSTRLocalControl: contains the program that controls the NAFSTR.
        * NARFSTRLocalControl: contains the program that controls the NARFSTR.
        * TestCode: contains test code.
* Documentation: contains all the documentation.
    * build: contains all the files for the built documentation.
    * source: contains all the source files for the documentation.
* DocumentationOld: contains the communication protocol for the NAFSTR, which is old.
* Final STL Files: contains all the STL files need to print parts for the robot.
* Fingerprints: contains all the files relating to fingerprints for the two robots.
    * NAFSTRSimpleFingerprints: contains all the files for the NAFSTR's fingerprints.
    * NARFSTRRolledFingerprints: contains all the files for the NARFSTR's rolled fingerprints.
    * PointCloudFingerprints: contains all the files for point cloud fingerprints.
* Improvements: contains files relating to improvements that need to be done.
* Pictures: contains pictures of the robots.
* Renderings: contains renderings of the robot.
* RobotLabels: contains files related to the information labels on the robots.
* Sketchup Files: contains the design files for the robots.
* Test STL Files: contains all the STL files used in test prints.
* Test Moves: contains text files with test commands for the two robots.
* Videos: contains videos of the robots.