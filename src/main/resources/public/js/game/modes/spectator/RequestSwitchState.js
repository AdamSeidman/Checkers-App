/**
 * This module exports the RequestingBackupMove class constructor.
 *
 * This component is an concrete implementation of a state
 * for the Game view; this state represents the view state
 * in which the player has created a non-empty Turn.  From
 * this state the user may request another move or to submit
 * the current set of moves as a single turn.
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
  var RequestSwitchState = function(controller) {
    // private attributes
    this._controller = controller;
  };

  //
  // Public (external) methods
  //

  /**
   * Get the name of this state.
   */
  RequestSwitchState.prototype.getName = function getName() {
    return SpectatorModeConstants.REQUEST_SWITCH;
  }

  /**
   * Hook when entering this state.
   */
  RequestSwitchState.prototype.onEntry = function onEntry() {

    // 2) disable all Pieces
    this._controller.disableAllMyPieces();

    // 3) ask the server to backup from the most recent move
    jQuery.post('/switchSides', '')
    // HTTP success handler
    .done(handleResponse.bind(this))
    // HTTP error handler
    .fail(AjaxUtils.handleErrorResponse)
    // always display a message that the Ajax call has completed.
    .always(() => console.debug('SwitchSides response complete.'));

    // helper function (Ajax success callback)
    function handleResponse(message) {
        if (message.type === 'info') {
            window.location = '/game';
        }
        // handle error message
        else {
            // There are valid error conditions, such as not completing
            // a jump sequence.
            this._controller.displayMessage(message);
        }
    }
  }

  // export class constructor
  return RequestSwitchState;

});
