// Fingerprint parameters
imageResolution = 19.7; //dots per mm
imageX = 416; //px
imageY = 560; //px
file = "FGen1.png";
fingerprintX = 30; //mm
fingerprintY = 30; //mm
fingerprintScale = 1.4;

//Other parameters (these should not be changed)
fingerprintRidgeHeight = .2; //mm
fingerprintBaseThickness = .2; //mm
base = .4; //mm
imageScaleFactor = 1/imageResolution;
hotFudge = 20; //mm
fingerEndWingLength = 69.365;//-X side
motorEndWingLength = 52.163;//+X side
//wingLength = 20;//4.7;
wingWidth = 1.8;
wingHeight = 2.4; //Including base, mm
spongeHeight = 3;
spongeCellWidth = 2.18;
spongeCellHeight = 1.2;
spongeWall=.2;
spongeLength = fingerprintY;
totalLength = 39;//36;

union() {
    translate([0, 0, spongeHeight+base-fingerprintBaseThickness])
        union() {
            sponge (spongeCellWidth, spongeCellHeight, spongeWall, spongeWall, fingerprintX, spongeLength, spongeHeight);
            translate([0,0,fingerprintRidgeHeight+fingerprintBaseThickness])
                fingerprint(file, fingerprintScale, fingerprintX, fingerprintY, fingerprintRidgeHeight, fingerprintBaseThickness);
        };
   // translate([fingerprintX / 2, -totalLength / 2, 0]) cube([fingerEndWingLength, totalLength, base], center = false);
    translate([-fingerprintX / 2-motorEndWingLength, -totalLength / 2, 0]) cube([motorEndWingLength+fingerprintX+fingerEndWingLength, totalLength, base], center = false);
    translate([fingerprintX / 2 + fingerEndWingLength - wingWidth, -totalLength / 2, 0]) cube([wingWidth, totalLength, wingHeight]);
    translate([-fingerprintX / 2 - motorEndWingLength, -totalLength / 2, 0]) cube([wingWidth, totalLength, wingHeight]);

    //fingerprint("FGen1.png", 1, 24, 24, .2, .2);
}

module fingerprint(fname, scale, xDim, yDim, ridgeThickness, baseThickness) {
    union() {
        intersection() {
            scale([scale,scale,1],center=true)
            resize(newsize=[imageX*imageScaleFactor,imageY*imageScaleFactor, ridgeThickness])
                surface(file = fname, center = true, invert = true);;
            cube([xDim, yDim, ridgeThickness * 2], center = true);
        }
        translate([-xDim/2, -yDim/2, -2*baseThickness])
            cube([xDim, yDim, baseThickness]);
    }
}

module sponge(holeSize, sliceSize, strutSize, slabSize, xDim, yDim, zDim) {

  n = (zDim - slabSize) / (slabSize + sliceSize);

  size = [xDim, yDim, slabSize * (n + 1) + sliceSize * n]; //x,y,z size

  translate([-xDim / 2, -yDim / 2, -( slabSize * (n + 1) + sliceSize * n)])
  union() {
    difference() {
      cube(size);

      for (z = [slabSize: sliceSize + slabSize: size[2] - slabSize]) {
        for (x = [0: holeSize + strutSize: size[0]]) {
/*
          if (round(x / (holeSize + strutSize)) % 2 == 1 && z == slabSize) {
            translate([x + strutSize, -hotFudge/2, -hotFudge])
            cube([holeSize, size[1]+hotFudge, sliceSize + slabSize+hotFudge]);
          } else
         */
          if (round((z - slabSize) / (sliceSize + slabSize)) % 2 == 0) {
            //if (x < size[0] - holeSize)o {
            translate([x + strutSize, -hotFudge/2, z])
            cube([holeSize, size[1]+hotFudge, sliceSize]);
            //}
          } else {
            translate([x - (holeSize / 2) + strutSize / 2, -hotFudge/2, z])
            cube([holeSize, size[1]+hotFudge, sliceSize]);
          }

          //echo(x = x, z = z);
        }
      }

    }
    cube([strutSize, size[1], size[2]]);
    translate([size[0] - strutSize, 0, 0])
    cube([strutSize, size[1], size[2]]);
  };
}