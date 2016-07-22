using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace CSharpLibrary
{
    class PositionMove : Move
    {
        private int position;
        private int time;

        //Makes a new position move with the specified position and time.
        public PositionMove(int pos, int t)
        {
            this.position = pos;
            this.time = t;
        }

        public override string toString()
        {
            return "pos " + position + " " + time;
        }
    }
}
