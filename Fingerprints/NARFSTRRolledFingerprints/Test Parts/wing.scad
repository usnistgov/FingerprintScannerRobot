
imageResolution = 19.7; //dots per mm
imageScaleFactor = 1/imageResolution;
imageX = 416; //px
imageY = 560; //px
imageHeight = 100; //mm
hotFudge = 20; //mm

file = "FGen1.png";
fingerprintX = 30;
fingerprintY = 30;
fingerprintScale = 1.4;
fingerprintRidgeHeight = .2;
fingerprintBaseThickness = .2;
base = .4;
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
totalLength = 30;//36;

union() {
    translate([-fingerprintX / 2-fingerEndWingLength, -totalLength / 2, 0]) cube([fingerEndWingLength, totalLength, base], center = false);
    translate([-fingerprintX / 2 - fingerEndWingLength, -totalLength / 2, 0]) cube([wingWidth, totalLength, wingHeight]);

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

          if (round(x / (holeSize + strutSize)) % 2 == 1 && z == slabSize) {
            translate([x + strutSize, 0, 0])
            cube([holeSize, size[1], sliceSize + slabSize]);
          } else if (round((z - slabSize) / (sliceSize + slabSize)) % 2 == 0) {
            //if (x < size[0] - holeSize)o {
            translate([x + strutSize, 0, z])
            cube([holeSize, size[1], sliceSize]);
            //}
          } else {
            translate([x - (holeSize / 2) + strutSize / 2, 0, z])
            cube([holeSize, size[1], sliceSize]);
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