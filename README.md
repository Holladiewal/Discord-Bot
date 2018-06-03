# DiscoRobot
This ReadMe is a WIP. Expect more at a later point.

# Keys
## Current keys:
* "${cmdlist}"  
  lists all available commands on this guild (just their names)
* "%n" / "%{nick}  
  will be replaced with the nick name of the user calling this command
* "%m" / "%{mention}  
 mentions the user calling this command
* "%{pm}"
  sends the whole message to the calling user in a pm
* "%{params:\< number or "all"\>}"  
  returns the parameter at the given zero-based index or all of them
* "%{redirect:<channelID>}"  
  sends the whole message in the channel denoted by its channelID on the current guild only
* "%{classneeded:\<number\>}"  
  limits usage of this command to a class (1,2,3,4,5) only
* "%{excluderole:\<ID\>}" / "%{roleneeded:\<ID\>}"  
  exludes or limits the execution of a command to a certain set of roles. members of class 4 and 5 can alway run these commands
* "%{togglerole:\< role name \>}"  
  toggles the role on the executing user
* "%{giverole:\< role name\>}" / "%{takerole:\< role name \>}"  
  gives or takes the specified role to the executing user
* "%{createrole:\< name \>}" / "%{deleterole:\< name \>}"  
  creates or deletes the first - matching role
* "%{for:\<varname\>:\<min\>:\<max\>:\<stepSize\>:\<commands to execute seperated by new-line\>}"   
  basic for loop with custom stepSize
* "%{withjson:\<url\>}"  
  gets a json from the specified url
* "%{getFromJson:\<RegEx-JSON-path to key\>}"   
   returns the last-matching JSON key from the last loaded JSON
* "${arr:\<name\>:\<index\>}"   
  gets the String at the index in the specified array
* "${var:\<name\>}"  
  returns the String saved with the given name
* "%{clear: \< amount to clear \>"  
  deletes the last x messages
* "%{outcome:\< command \>"  
  returns the errorcode of the command instead of the return value
* "%{suppress:\< command \>}"  
  prevents printing of the return value of the command
* "%{print:\< command \>}"  
  prints the return value of the command, works in suppressed blocks
* "%{simplemath:\< term \>"  
  returns the result of the given expression, only ONE operator supported at a time


# Integrated commands  
By default two command names are blocked: "bot" and "command"

## command command:
* add \<name\> \<response\>
  adds a command with the given response
* del / delete \<name\>
  delete the command with the given name
* modify \< name \> \< response \>
  modifies the given command and sets it's response accordingly
* inspect \< name \>
  prints the response of the given command without any parsing

## bot command:
* commandchar
  * get:  
    responds with the current commandchar (prefix for commands)
  * set:  
    sets the commandchar
* qlineaction
  * get:  
    responds with the current qline action
  * set:  
    sets the qline action. accepted values are "kick" and "ban"
* timeoutrole
  * get:
    responds with the current name of the timeout role
  * set:
    sets the name of the timeout role to the given name
* timeoutchannel
  * get:
    responds with the current name of the timeout channel
  * set:
    sets the tmeout channel to the first-matching channel
* nick
  * qline
    * add / set:  
      adds a nick to the qline list. Every nick change to a nick on this list, will revert the nick change and kick or ban the user, depending on the qline action setting.
    * remove:  
      removes a nick from qline list.
  * block
    * add / set:  
      adds a nick to the list of blocked nicks. Every nick change to this list will be reverted.
    * remove / unblock:  
      removes a nick from the list of blocked nicks.
  * claim
    * add / claim:  
      claims the given nick for you. Only you can change to this nick, a nick change from another user to this nick will be reverted. You is determined by the combination of your discord username and your discriminator.
    * unclaim / free:  
      free's the given nick from a claim and returns it to the open nick pool. Only you or an server admin can free a nick.
* roles / clearaces
  * add
    * 1:  
      adds the given role id to class 1 ("regulars")
    * 2:  
      adds the given role id to class 2 ("bot manager")
    * 3:  
      adds the given role id to class 3 ("moderator")
    * 4:  
      adds the given role id to class 4 ("admin")
  * remove 
    * 1:  
      removes the given role id from class 1
    * 2:  
      removes the given role id from class 2
    * 3:  
      removes the given role id from class 3
    * 4:  
      removes the given role id from class 4
      
