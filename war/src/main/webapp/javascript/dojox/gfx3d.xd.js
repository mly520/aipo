dojo._xdResourceLoaded({
depends: [["provide", "dojox.gfx3d"],
["require", "dojox.gfx3d.matrix"],
["require", "dojox.gfx3d._base"],
["require", "dojox.gfx3d.object"]],
defineResource: function(dojo){if(!dojo._hasResource["dojox.gfx3d"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dojox.gfx3d"] = true;
dojo.provide("dojox.gfx3d");

dojo.require("dojox.gfx3d.matrix");
dojo.require("dojox.gfx3d._base");
dojo.require("dojox.gfx3d.object");



}

}});