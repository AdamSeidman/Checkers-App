/**
 * This module exports a map of constants used in the SPECTATOR mode.
 */
define(function(){
    'use strict';

    /**
     * This module is a map of constant symbols to their names.
     * Used in methods to change GameView Spectator mode states.
     */
    return {
      
      SPECTATOR_MODE_STARTING: 'SPECTATOR_MODE_STARTING',
      WAIT_FOR_MOVE: 'Waiting for Move',
      REQUEST_SWITCH: 'Requesting Switch Views',
      REQUESTING_LEAVE: 'Request to return to Home page'

      //
      // Buttons
      //

      ,SWITCH_BUTTON_ID: 'switchBtn'
      ,SWITCH_BUTTON_TOOLTIP: 'Switch your spectator view.'
      ,LEAVE_BUTTON_ID: 'leaveBtn'
      ,LEAVE_BUTTON_TOOLTIP: 'Go back to the home page.'

    };
});