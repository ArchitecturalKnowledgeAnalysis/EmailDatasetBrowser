#!/usr/bin/env dub
/+ dub.sdl:
    dependency "dsh" version="~>1.6.1"
+/

/** 
 * This updater script can be used to quickly tag the current branch and push
 * that tag to the remote origin.
 */
module updater;

import dsh;

int main(string[] args) {
    if (args.length < 2) {
        error("Missing required tag argument.");
        return 1;
    }

    import std.string;
    import std.regex;
    auto tagRegex = ctRegex!(`^v\d+(?:\.\d+)*$`);
    string tag = args[1].strip;
    if (!matchFirst(tag, tagRegex)) {
        error("The tag \"%s\" is not valid.", tag);
        return 1;
    }

    writefln!"Tag: %s"(tag);
    runOrQuit("git tag " ~ tag);
    writeln("Created tag.");
    runOrQuit("git push origin " ~ tag);
    writeln("Pushed tag to remote repository.");
    return 0;
}
