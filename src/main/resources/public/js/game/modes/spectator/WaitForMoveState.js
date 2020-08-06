/**
 * This module exports the EmptyTurnState class constructor.
 *
 * This component is an concrete implementation of a state
 * for the Game view; this state represents the view state
 * in which the player has not yet made a move or have backed-up
 * all preceding moves.
 */
define(function(require){
  'use strict';

  // imports
  var SpectatorModeConstants = require('./SpectatorModeConstants');
  var AjaxUtils = require('../../util/AjaxUtils');

  /**
   * Constructor function.
   *
   * @param {PlayController} controller
   *    The Play mode controller object.
   */
  var WaitForMoveState = function(controller) {
      this._controller = controller;
  };

  //
  // Public (external) methods
  //

  /**
   * Get the name of this state.
   */
  WaitForMoveState.prototype.getName = function getName() {
    return SpectatorModeConstants.WAIT_FOR_MOVE;
  }

  /**
   * Method when entering this state.
   */
  WaitForMoveState.prototype.onEntry = function onEntry() {
        jQuery.post('/checkTurn', '')
        // HTTP success handler
        .done(handleResponse.bind(this))
        // HTTP error handler
        .fail(AjaxUtils.handleErrorResponse)
        // always display a message that the Ajax call has completed.
        .always(() => console.debug('CheckTurn response complete.'));

        // helper function (Ajax success callback)
        function handleResponse(message) {
            if (message.type === 'info') {
                if(message.text == 'true') {
                    // If move has been made, reload the page.
                    var sound = document.getElementById("audio");
                    sound.play();
                    window.location = '/game';
                 } else {
                    window.location = '/gameEnd';
                 }
            }
            else {
                // Wait another 3 seconds
                this._controller.setState(SpectatorModeConstants.SPECTATOR_MODE_STARTING);
            }
        }
  }

  // export class constructor
  return WaitForMoveState;

});
