# tupelo-client

A Clojure library for using the Tupelo distributed ledger system.

Specifically it allows you to manage Tupelo chain trees and submit chain tree
transactions to a notary group for verification. It does this by communicating
with a Tupelo RPC server.

## Usage

Set as a dependency in your project:

Latest release: 0.1.0-SNAPSHOT

tools-deps:

```
com.quorumcontrol/tupelo-client {:mvn/version "0.1.0-SNAPSHOT"}
```

Leiningen:

```
[com.quorumcontrol/tupelo-client "0.1.0-SNAPSHOT"]
```

## License

Copyright © 2018-2019 Quorum Control, GmbH

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.