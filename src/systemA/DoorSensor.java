package systemA;

import common.Configuration;
import MessagePackage.*;



class DoorSensor{

	public static void main(String args[]){
		String MsgMgrIP;					// Message Manager IP address
		MessageManagerInterface em = null;

		/////////////////////////////////////////////////////////////////////////////////
		// Get the IP address of the message manager
		/////////////////////////////////////////////////////////////////////////////////

		if ( args.length == 0 ){
			// message manager is on the local system

			System.out.println("\n\nAttempting to register on the local machine..." );

			try{
				// Here we create an message manager interface object. This assumes
				// that the message manager is on the local machine

				em = new MessageManagerInterface();
			}

			catch (Exception e){
				System.out.println("Error instantiating message manager interface: " + e);

			} // catch

		} else {

			// message manager is not on the local system

			MsgMgrIP = args[0];

			System.out.println("\n\nAttempting to register on the machine:: " + MsgMgrIP );

			try{
				// Here we create an message manager interface object. This assumes
				// that the message manager is NOT on the local machine

				em = new MessageManagerInterface( MsgMgrIP );
			}

			catch (Exception e){
				System.out.println("Error instantiating message manager interface: " + e);

			} // catch

		} // if
		
		if (em != null){
			float winPosX = 0.25f; 	//This is the X position of the message window in terms
			//of a percentage of the screen height
			float winPosY = 0.0f;	//This is the Y position of the message window in terms
			//of a percentage of the screen height

			Sensor sensor = new Sensor("Door Sensor", winPosX, winPosY, em, "Doorbreak detected", "Door sensor will alarm if any door break is detect.", Configuration.DOOR_SENSOR_ID, "Door break detected!");
			sensor.execute();

		} else {
			System.out.println("Unable to register with the message manager.\n\n" );

		} // if

	} // main

} // Door Sensor