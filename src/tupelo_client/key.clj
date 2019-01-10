(ns tupelo-client.key
  (:require [tupelo-client.credentials :as creds])
  (:import (walletrpc TupeloRpc$GenerateKeyRequest
                      WalletRPCServiceGrpc$WalletRPCServiceBlockingStub)))

(defn generate [^WalletRPCServiceGrpc$WalletRPCServiceBlockingStub client
                {wallet-name :walletName, pass-phrase :passPhrase}]
  (let [req (-> (TupeloRpc$GenerateKeyRequest/newBuilder)
                (creds/set wallet-name pass-phrase)
                .build)
        resp (.generateKey client req)]
    {:key-addr (.getKeyAddr resp)}))

