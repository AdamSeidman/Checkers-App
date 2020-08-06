/**
 * This module exports the SpectatorController class constructor.
 * 
 * This component does...
 */
define(function(require){
  'use strict';
  
  // imports
  var StatePatternMixin = require('../../util/StatePatternMixin');
  var ControlsToolbarMixin = require('../../util/ControlsToolbarMixin');
  var AjaxUtils = require('../../util/AjaxUtils');
  var SpectatorModeConstants = require('./SpectatorModeConstants');
  
  // import SPECTATOR mode states
  var SpectatorModeStartState = require('./SpectatorModeStartState');
  var RequestSwitchState = require('./RequestSwitchState');
  var WaitForMoveState = require('./WaitForMoveState');

  /**
   * Constructor function.
   */
  function SpectatorController(view, gameState) {
    // Add the StatePattern mixin
    StatePatternMixin.call(this);
    
    // create states and a lookup map
    this.addStateDefinition(SpectatorModeConstants.SPECTATOR_MODE_STARTING,
            new SpectatorModeStartState(this));
    this.addStateDefinition(SpectatorModeConstants.REQUEST_SWITCH,
           new RequestSwitchState(this));
    this.addStateDefinition(SpectatorModeConstants.WAIT_FOR_MOVE,
            new WaitForMoveState(this));
    
    // Add the ModeControls mixin
    ControlsToolbarMixin.call(this);
    this.addButton(SpectatorModeConstants.SWITCH_BUTTON_ID, 'Switch Views', true,
            SpectatorModeConstants.SWITCH_BUTTON_TOOLTIP, this.switchPressed);
    this.addButton(SpectatorModeConstants.LEAVE_SWITCH_BUTTON_ID, "Leave Game", true,
            SpectatorModeConstants.LEAVE_SWITCH_TOOLTIP, this.leaveRequested);

    var helperText = "You are watching a game between " + gameState.getRedPlayer() + " and " + gameState.getWhitePlayer() + ".";
    view.setHelperText(helperText);

    // Public (internal) methods

    /**
     * Start Spectator mode.
     */
    this.startup = function startup() {
      // start Spectator mode
      this.setState(SpectatorModeConstants.SPECTATOR_MODE_STARTING);
    }
    
  };

  //
  // Public (external) methods
  //

    SpectatorController.prototype.switchPressed = function submitTurn() {

       // if confirmed, then send the resignation command to the server
       jQuery.post('/switchSides', '')
       // HTTP success handler
       .done(handleResponse.bind(this))
       // HTTP error handler
       .fail(AjaxUtils.handleErrorResponse)
       // always display a message that the Ajax call has completed.
       .always(() => console.debug('SwitchSides response complete.'));


       function handleResponse(message) {
         if (message.type === 'info') {
           // tell the browser to route the player to the Home page
           window.location = '/game';
         }
         // handle error message
         else {
           this.displayMessage(message);
         }
       }

       this._delegateStateMessage('switchPressed', arguments);
    }

     SpectatorController.prototype.leaveRequested = function leaveTurn() {

            var yes = window.confirm('Are you sure you want to discontinue spectating?');
            if (!yes) {
                // if not, then return
                return;
            }

           // if confirmed, then send the resignation command to the server
           jQuery.post('/endSpectate', '')
           // HTTP success handler
           .done(handleResponse.bind(this))
           // HTTP error handler
           .fail(AjaxUtils.handleErrorResponse)
           // always display a message that the Ajax call has completed.
           .always(() => console.debug('LeaveRequest response complete.'));


           function handleResponse(message) {

             if (message.type === 'info') {
               // tell the browser to route the player to the Home page
               window.location = '/';
             }
             // handle error message
             else {
               this.displayMessage(message);
             }
           }
           this._delegateStateMessage('switchPressed', arguments);
        }

  // export class constructor
  return SpectatorController;
  
});
