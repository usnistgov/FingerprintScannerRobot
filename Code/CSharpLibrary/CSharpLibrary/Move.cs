using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading.Tasks;

namespace CSharpLibrary
{
    abstract class Move
    {
        //Returns the move in the format the Arduino requires.
        public abstract String toString();
    }
}
