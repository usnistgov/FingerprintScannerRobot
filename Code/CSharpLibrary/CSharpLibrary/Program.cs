using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.NetworkInformation;
using System.Net.Sockets;
using System.Text;
using System.Threading.Tasks;

namespace CSharpLibrary
{
    class Program
    {
        static void Main(string[] args)
        {
            //List<IPAddress> robots = NAFSTR.getRobots();
            //Console.WriteLine(string.Join("\n", robots));

            NAFSTR robot = new NAFSTR(IPAddress.Parse("10.0.34.100"));//robots[0]);
            robot.Hold();

            //robot.Set(0, new SensorMove(-30, 40, 50, 60, 1000), new PositionMove(90, 1000));

            robot.Set(0, new PositionMove(60, 1000), new PositionMove(90, 1000));
            robot.Set(1, new PositionMove(60, 1000), new PositionMove(90, 1000));
            robot.Set(2, new PositionMove(140, 1000), new PositionMove(106, 1000));
            robot.Set(3, new PositionMove(140, 1000), new PositionMove(106, 1000));

            while (true)
            {
                Console.WriteLine(string.Join("\n", robot.Move()));
            }

            //Console.WriteLine("END");
            //Console.Read();
        }
    }
}