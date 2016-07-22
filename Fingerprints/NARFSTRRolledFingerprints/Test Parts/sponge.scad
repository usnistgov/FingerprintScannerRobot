module sponge(holeSize, sliceSize, strutSize, slabSize, xDim, yDim, n) {

  size = [xDim, yDim, slabSize * (n + 1) + sliceSize * n]; //x,y,z size

  union() {
    difference() {
      cube(size);

      for (z = [slabSize: sliceSize + slabSize: size[2] - slabSize]) {
        for (x = [0: holeSize + strutSize: size[0]]) {

          if (round((z - slabSize) / (sliceSize + slabSize)) % 2 == 0) {
            //if (x < size[0] - holeSize) {
            translate([x + strutSize, 0, z])
            cube([holeSize, size[1], sliceSize]);
            //}
          } else {
            translate([x - (holeSize / 2) + strutSize / 2, 0, z])
            cube([holeSize, size[1], sliceSize]);
          }

          echo(x = x, z = z);
        }
      }

    }
    cube([strutSize, size[1], size[2]]);
    translate([size[0] - strutSize, 0, 0])
    cube([strutSize, size[1], size[2]]);
  }
}

sponge (2.18, 1.2, .2, .2, 24, 24, 3);