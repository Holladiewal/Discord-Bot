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
* "%{redirect:<channelID>}"
  sends the whole message in the channel denoted by its channelID on the current guild only

## Planned Keys:
* "%{classneeded}"
  limits usage of this command to a class (1,2,3,4,5) only
* "%{excluderole}" / "%{roleonly}"
  exludes or limits the execution of a command to a certain set of roles. members of class 4 and 5 can alway run these commands
* "%{withjson:<url>}"
  gets a json from the specified url and makes it available in an array
* "${arr:<name>:<index>}" 
  gets the String at the index in the specified array
* "%{for:<varname>:<min>:<max>:<stepSize>}" and "%{endfor}"
  basic for loop with custom stepSize
* and more!

# Integrated commands  
By default two command names are blocked: "bot" and "command"
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
      
