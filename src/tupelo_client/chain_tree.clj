(ns tupelo-client.chain-tree
  (:require [clj-cbor.core :as cbor]
            [tupelo-client.credentials :as creds]
            [clojure.string :as str])
  (:import (com.quorumcontrol.tupelo.walletrpc
            TupeloRpc$GenerateChainRequest
            WalletRPCServiceGrpc$WalletRPCServiceBlockingStub
            TupeloRpc$SetDataRequest TupeloRpc$ResolveRequest
            TupeloRpc$SetOwnerRequest TupeloRpc$EstablishTokenRequest TupeloRpc TupeloRpc$MintTokenRequest)
           (com.google.protobuf ByteString))
  (:refer-clojure :exclude [resolve]))

(def chaintree-data-path "/tree/data")

(defn create [^WalletRPCServiceGrpc$WalletRPCServiceBlockingStub client
              {:keys [wallet-name pass-phrase]} key-addr]
  (let [req (-> (TupeloRpc$GenerateChainRequest/newBuilder)
                (creds/set wallet-name pass-phrase)
                (.setKeyAddr key-addr)
                .build)
        resp (.createChainTree client req)]
    {:chain-tree-id (.getChainId resp)}))

(defn set-data [^WalletRPCServiceGrpc$WalletRPCServiceBlockingStub client
                {:keys [wallet-name pass-phrase]} chain-id key-addr path data]
  (let [req (-> (TupeloRpc$SetDataRequest/newBuilder)
                (creds/set wallet-name pass-phrase)
                (.setChainId chain-id)
                (.setKeyAddr key-addr)
                (.setPath path)
                (.setValue (ByteString/copyFrom (cbor/encode data)))
                .build)
        resp (.setData client req)]
    {:tip (.getTip resp)}))

(defn resolve [^WalletRPCServiceGrpc$WalletRPCServiceBlockingStub client
               {:keys [wallet-name pass-phrase]} chain-id path]
  (let [req (-> (TupeloRpc$ResolveRequest/newBuilder)
                (creds/set wallet-name pass-phrase)
                (.setChainId chain-id)
                (.setPath path)
                .build)
        resp (.resolve client req)
        data {:data (-> resp .getData .newInput cbor/decode)}]
    (let [remaining-path (.getRemainingPath resp)]
      (if (not (str/blank? remaining-path))
        (assoc data :remaining-path remaining-path)
        data))))

(defn path->components [path]
  (-> path
      (str/replace #"^/" "")
      (str/split #"/")))

(defn data-path [path]
  (->> path
       path->components
       (concat (path->components chaintree-data-path))
       (str/join "/")))

(defn resolve-data [^WalletRPCServiceGrpc$WalletRPCServiceBlockingStub client
                    creds chain-id path]
  (resolve client creds chain-id (data-path path)))

(defn change-owner [^WalletRPCServiceGrpc$WalletRPCServiceBlockingStub client
                    {:keys [wallet-name pass-phrase]} chain-id key-addr
                    new-owner-keys]
  (let [req-builder (-> (TupeloRpc$SetOwnerRequest/newBuilder)
                        (creds/set wallet-name pass-phrase)
                        (.setChainId chain-id)
                        (.setKeyAddr key-addr))
        _ (doseq [k new-owner-keys]
            (.addNewOwnerKeys req-builder k))
        req (.build req-builder)
        resp (.setOwner client req)]
    {:tip (.getTip resp)}))

(defn establish-token [^WalletRPCServiceGrpc$WalletRPCServiceBlockingStub client
                       {:keys [wallet-name pass-phrase]} chain-id key-addr
                       token-name {:keys [maximum] :as monetary-policy}]
  (let [req (-> (TupeloRpc$EstablishTokenRequest/newBuilder)
                (creds/set wallet-name pass-phrase)
                (.setChainId chain-id)
                (.setKeyAddr key-addr)
                (.setTokenName token-name)
                (.setMaximum maximum)
                .build)
        resp (.establishToken client req)]
    {:tip (.getTip resp)}))

(defn mint-token [^WalletRPCServiceGrpc$WalletRPCServiceBlockingStub client
                  {:keys [wallet-name pass-phrase]} chain-id key-addr
                  token-name amount]
  (let [req (-> (TupeloRpc$MintTokenRequest/newBuilder)
                (creds/set wallet-name pass-phrase)
                (.setChainId chain-id)
                (.setKeyAddr key-addr)
                (.setTokenName token-name)
                (.setAmount amount)
                .build)
        resp (.mintToken client req)]
    {:tip (.getTip resp)}))
