using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace CSharpLibrary
{
    class SensorMove : Move
    {
        private int velocity;
        private int lightMin;
        private int lightMax;
        private int posMax;
        private int waitTime;

        //Makes a new sensor move with the specified information.
        public SensorMove(int v, int lmin, int lmax, int pmax, int t)
        {
            velocity = v;
            lightMin = lmin;
            lightMax = lmax;
            posMax = pmax;
            waitTime = t;
        }
        public override string toString()
        {
            return "sen " + velocity + " " + lightMin + " " + lightMax + " " + posMax + " " + waitTime;
        }
    }
}
