using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.IO;
using System.Linq;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading.Tasks;

namespace CSharpLibrary
{

    //A class for communicating with the NAFSTR.
    class NAFSTR
    {

        private const int PORT = 80;
        private const string CHALLENGE = "fingerrobot";
        private const string RESPONSE = "youfoundme";
        private const int NUM_SERVOS = 4;

        //The socket used for communicaiton.
        private Socket s;
        //The output stream.
        private StreamWriter stream;
        //The input stream.
        private StreamReader input;

        //Makes a new NAFSTR at the specified IP address.
        public NAFSTR(IPAddress a)
        {

            //Make a TCP socket
            s = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
            IPEndPoint ipe = new IPEndPoint(a, PORT);
            s.Connect(ipe);

            //Make the streams
            NetworkStream ns = new NetworkStream(s);
            stream = new StreamWriter(ns);
            input = new StreamReader(ns);
        }

        //Sends a relax command and waits for completion.
        public void Relax()
        {
            stream.WriteLine("relax");
            stream.Flush();
            while (!input.ReadLine().Equals("relax-end")) ;
        }

        //Sends a hold command and waits for completion.
        public void Hold()
        {
            stream.WriteLine("hold");
            stream.Flush();
            while (!input.ReadLine().Equals("hold-end")) ;
        }

        //Sends a get command and waits for completion. Returns the result of the get command.
        public int Get(int servo, SENSOR_TYPES type)
        {
            if (type == SENSOR_TYPES.FSR)
            {
                stream.WriteLine("get " + servo + " f");
            }
            else
            {
                stream.WriteLine("get " + servo + " l");
            }
            stream.Flush();
            while (!input.ReadLine().Equals("get-received")) ;
            int result = int.Parse(input.ReadLine());
            while (!input.ReadLine().Equals("get-end")) ;
            return result;
        }

        //Sends a move command for the specified servos. Returns a list of all the output.
        public List<String> Move(params int[] servos)
        {

            //Send the command
            List<String> result = new List<String>();
            stream.WriteLine("move " + servos.Length);
            for (int x = 0; x < servos.Length; x++)
            {
                stream.WriteLine(" " + servos[x]);
            }
            stream.Flush();

            //Wait for completion
            while (!input.ReadLine().Equals("move-received")) ;
            String data;
            while (!(data = input.ReadLine()).Equals("move-end"))
            {
                result.Add(data);
            }
            return result;
        }

        //Moves all the servos.
        public List<String> Move()
        {
            int[] servos = new int[NUM_SERVOS];
            for (int x = 0; x < servos.Length; x++)
                servos[x] = x;
            return Move(servos);
        }

        //Sets a move for a servo and waits for completion.
        public void Set(int servo, Move move1, Move move2)
        {
            stream.WriteLine("set " + servo + " ");
            stream.WriteLine(move1.toString() + " ");
            stream.WriteLine(move2.toString() + " ");
            stream.Flush();
            while (!input.ReadLine().Equals("set-end")) ;
        }

        //Discovers robots on the network.
        public static List<IPAddress> getRobots()
        {

            //Set up the UDP socket
            IPEndPoint groupEP = new IPEndPoint(IPAddress.Parse("255.255.255.255"), PORT);
            Socket udpSock = new Socket(AddressFamily.InterNetwork, SocketType.Dgram, ProtocolType.Udp);

            //Prevent fragmenting and enable broadcast
            udpSock.DontFragment = true;
            udpSock.EnableBroadcast = true;

            //Send the bytes
            udpSock.SendTo(Encoding.ASCII.GetBytes("fingerrobot"), groupEP);

            //Set up the TCP server
            IPHostEntry host;
            //Find the local IP.
            string localIP = "";
            host = Dns.GetHostEntry(Dns.GetHostName());
            foreach (IPAddress ip in host.AddressList)
            {
                if (ip.AddressFamily.ToString() == "InterNetwork")
                {
                    localIP = ip.ToString();
                }
            }
            TcpListener listener = new TcpListener(IPAddress.Parse(localIP), PORT);
            listener.Start();

            //Set up a stopwatch to check for timeouts.
            Stopwatch s = new Stopwatch();
            s.Restart();
            String response = "youfoundme";
            List<IPAddress> result = new List<IPAddress>();

            //Keep going until 2 seconds have elapsed and no response has come.
            while (s.Elapsed.TotalMilliseconds < 2000)
            {
                if (listener.Pending())
                {
                    //Get the data
                    Socket theSock = listener.AcceptSocket();
                    theSock.ReceiveTimeout = 2000;
                    byte[] buffer = new byte[response.Length];
                    theSock.Receive(buffer);

                    //If we have a match, store the IP address.
                    if (Encoding.ASCII.GetString(buffer).Equals(response))
                    {
                        IPEndPoint end = theSock.RemoteEndPoint as IPEndPoint;
                        result.Add(end.Address);
                    }
                    //Restart the timer.
                    s.Restart();
                }
            }
            return result;
        }
    }
}
