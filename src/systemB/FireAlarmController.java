package systemB;
/******************************************************************************************************************
* File:FireAlarmController.java
* Course: 17655
* Project: Assignment A3
* Versions:
*	1.0 March 2014 - Initial rewrite of original assignment 3 (Yury Chernusheko).
*
* Description:
*
* This class simulates a device that controls fire alarm sensor and sprinkler controller. It polls the message manager for message
* ids = 11 and reacts to them by turning on or off sprinkler. The following command are valid
* strings for controlling the sprinkler:
*
*	F1 = FireAlarm on
*	F0 = FireAlarm off
*
* The state (on/off) is graphically displayed on the terminal in the indicator. Command messages are displayed in
* the message window. Once a valid command is recieved a confirmation message is sent with the id of -5 and the command in
* the command string.
*
* Parameters: IP address of the message manager (on command line). If blank, it is assumed that the message manager is
* on the local machine.
*
* Internal Methods:
*	static private void ConfirmMessage(MessageManagerInterface ei, String m )
*
******************************************************************************************************************/
import InstrumentationPackage.*;
import MessagePackage.*;
import common.Configuration;
import common.Device;

class FireAlarmController
{
	public static void main(String args[])
	{
		String MsgMgrIP;					// Message Manager IP address
		Message Msg = null;					// Message object
		MessageQueue eq = null;				// Message Queue
		MessageManagerInterface em = null;	// Interface object to the message manager
		int	Delay = 2500;					// The loop delay (2.5 seconds)
		boolean Done = false;				// Loop termination flag

		/////////////////////////////////////////////////////////////////////////////////
		// Get the IP address of the message manager
		/////////////////////////////////////////////////////////////////////////////////

 		if ( args.length == 0 )
 		{
			// message manager is on the local system

			System.out.println("\n\nAttempting to register on the local machine..." );

			try
			{
				// Here we create an message manager interface object. This assumes
				// that the message manager is on the local machine

				em = new MessageManagerInterface();
			}

			catch (Exception e)
			{
				System.out.println("Error instantiating message manager interface: " + e);

			} // catch

		} else {

			// message manager is not on the local system

			MsgMgrIP = args[0];

			System.out.println("\n\nAttempting to register on the machine:: " + MsgMgrIP );

			try
			{
				// Here we create an message manager interface object. This assumes
				// that the message manager is NOT on the local machine

				em = new MessageManagerInterface( MsgMgrIP );
			}

			catch (Exception e)
			{
				System.out.println("Error instantiating message manager interface: " + e);

			} // catch

		} // if

		// Here we check to see if registration worked. If em is null then the
		// message manager interface was not properly created.

		if (em != null)
		{
			System.out.println("Registered with the message manager." );

			/* Now we create the fire control status and message panel
			** We put this panel about 2/3s the way down the terminal, aligned to the left
			** of the terminal. The status indicators are placed directly under this panel
			*/

			float WinPosX = 0.25f; 	//This is the X position of the message window in terms
									//of a percentage of the screen height
			float WinPosY = 0.60f;	//This is the Y position of the message window in terms
								 	//of a percentage of the screen height

			MessageWindow mw = new MessageWindow("FireAlarm Controller Status Console", WinPosX, WinPosY);

			// Now we put the indicators directly under the FireAlarm control panel
			mw.WriteMessage("Registered with the message manager." );

	    	try
	    	{
				mw.WriteMessage("   Participant id: " + em.GetMyId() );
				mw.WriteMessage("   Registration Time: " + em.GetRegistrationTime() );

			} // try

	    	catch (Exception e)
			{
				System.out.println("Error:: " + e);

			} // catch

			/********************************************************************
			** Here we start the main simulation loop
			*********************************************************************/
	    	Device device = new Device(Configuration.critical_period, "Fire Alarm", "Receive fire alarm and resend to sprinkler controller and monitor", em);
			device.start();
	    	
			while ( !Done )
			{
				try
				{
					eq = em.GetMessageQueue();

				} // try

				catch( Exception e )
				{
					mw.WriteMessage("Error getting message queue::" + e );

				} // catch

				// If there are messages in the queue, we read through them.
				// We are looking for MessageIDs = 11, this is a request to turn the
				// FireAlarm on/off. Note that we get all the messages
				// at once... there is a 2.5 second delay between samples,.. so
				// the assumption is that there should only be a message at most.
				// If there are more, it is the last message that will effect the
				// output of the FireAlarm as it would in reality.

				int qlen = eq.GetSize();

				for ( int i = 0; i < qlen; i++ )
				{
					Msg = eq.GetMessage();

					if ( Msg.GetMessageId() == Configuration.FIRE_SENSOR_ID )
					{
						if (Msg.GetMessage().equalsIgnoreCase("F1")) // FireAlarm on
						{
							mw.WriteMessage("Received FireAlarm on message" );
							postMessage(em, "FA");
						} // if

					} // if

					// If the message ID == 99 then this is a signal that the simulation
					// is to end. At this point, the loop termination flag is set to
					// true and this process unregisters from the message manager.

					if ( Msg.GetMessageId() == 99 )
					{
						Done = true;

						try
						{
							em.UnRegister();

				    	} // try

				    	catch (Exception e)
				    	{
							mw.WriteMessage("Error unregistering: " + e);

				    	} // catch

				    	mw.WriteMessage( "\n\nSimulation Stopped. \n");

						// Get rid of the indicators. The message panel is left for the
						// user to exit so they can see the last message posted.

					} // if

				} // for


				try
				{
					Thread.sleep( Delay );

				} // try

				catch( Exception e )
				{
					System.out.println( "Sleep error:: " + e );

				} // catch

			} // while

		} else {

			System.out.println("Unable to register with the message manager.\n\n" );

		} // if

	} // main

	
	static private void postMessage(MessageManagerInterface ei, String m ){
		// Here we create the message.
		Message msg = new Message( Configuration.FIRE_CONTROLLER_ID, m );
		// Here we send the message to the message manager.
		try{
			ei.SendMessage( msg );
		} // try
		catch (Exception e){
			System.out.println("Error posting Message:: " + e);
		} // catch
	} // PostMessage
	
} // FireAlarmControllers