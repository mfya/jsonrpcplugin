# Synopsis 
A plugin for Universal Media Server, which allows to write simple plugins in languages other than Java. 
It forwards some methods of the [external interfaces](https://github.com/UniversalMediaServer/UniversalMediaServer/tree/master/src/main/java/net/pms/external) 
called by UMS to a (simplified) counterpart on an arbitrary JSON-RPC 2.0 server. 
Such a server can be easily setup in many (scripting) languages (See [Wikipedia](http://en.wikipedia.org/wiki/JSON-RPC#Implementations)).

This is a simple approach to add some scriptability to UMS and it has been created to quickly 
customize how web streams are listed, downloaded and transcoded - and all that while UMS keeps running.
The external interfaces provided by UMS are perfectly suited for these tasks, but available plugins
with scripting support are either incompatible to recent UMS versions (PMSEncoder) or are 
not directly focused on these tasks (Jumpy).


