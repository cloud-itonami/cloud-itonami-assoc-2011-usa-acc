(ns association-facts-test
  (:require [clojure.java.io :as io] [clojure.java.shell :as shell]
            [clojure.test :refer [deftest is testing]]
            [kotoba.compiler.core :as compiler] [kotoba.kir :as ir]))
(def source (slurp "src/association_facts.kotoba"))
(defn call [kir function & args] (ir/execute kir function (vec args)))
(defn present [option] (when (second option) (nth option 2)))
(def fields ["id" "title" "association" "isic" "country" "kind" "url"
             "url-provenance" "established-date" "retrieved-at"])
(def expected
  [{"id" "acc.our-150-years-history" "title" "Our 150 Years History"
    "association" "acc" "isic" "2011" "country" "USA" "kind" "governance-program"
    "url" "https://www.americanchemistry.com/about-acc/our-150-years-history"
    "url-provenance" "official-association-site" "established-date" "1872"
    "retrieved-at" "2026-07-16"}
   {"id" "acc.responsible-care-overview" "title" "Responsible Care Overview"
    "association" "acc" "isic" "2011" "country" "USA" "kind" "self-regulatory-code"
    "url" "https://www.americanchemistry.com/driving-safety-sustainability/responsible-care-driving-safety-sustainability/resources/responsible-care-overview"
    "url-provenance" "official-association-site" "established-date" "1988"
    "retrieved-at" "2026-07-16"}])

(deftest reference-preserves-year-precision-and-multiple-topics
  (let [kir (:kir (compiler/compile-source source :js-kotoba-v1))
        observed (mapv (fn [i] (into {} (map (fn [f] [f (present (call kir 'entry-field "acc" i f))]) fields))) [0 1])]
    (is (= expected observed))
    (is (= ["1872" "1988"] (mapv #(present (call kir 'entry-field "acc" % "established-date")) [0 1])))
    (is (= [1 2] (mapv #(call kir 'topic-count "acc" %) [0 1])))
    (is (= ["safety" "environment"] (mapv #(present (call kir 'topic "acc" 1 %)) [0 1])))
    (is (= "acc.responsible-care-overview" (present (call kir 'by-topic-id "acc" "safety" 0))))
    (is (= "acc.responsible-care-overview" (present (call kir 'by-topic-id "acc" "environment" 0))))
    (is (= #{} (set (:effects kir))))
    (testing "unknown values and invalid indexes fail closed"
      (is (zero? (call kir 'entry-count "wef")))
      (is (nil? (present (call kir 'entry-field "acc" -1 "id"))))
      (is (nil? (present (call kir 'entry-field "acc" 2 "id"))))
      (is (nil? (present (call kir 'entry-field "acc" 0 "unknown"))))
      (is (nil? (present (call kir 'topic "acc" 1 2))))
      (is (zero? (call kir 'by-topic-count "acc" "labor")))
      (is (nil? (present (call kir 'by-topic-id "acc" "safety" 1)))))))

(defn compiler-root []
  (nth (iterate #(.getParent ^java.nio.file.Path %)
                (java.nio.file.Path/of (.toURI (io/resource "kotoba/compiler/core.clj")))) 4))
(defn base64 [value] (.encodeToString (java.util.Base64/getEncoder) value))
(deftest restricted-javascript-and-typed-wasm-conform-semantically
  (let [javascript (compiler/compile-source source :js-kotoba-v1)
        wasm (compiler/compile-source source :wasm32-browser-kotoba-v1)
        js64 (base64 (.getBytes ^String (:source javascript) "UTF-8")) wasm64 (base64 ^bytes (:bytes wasm))
        probe (shell/sh "node" "--input-type=module" "-e"
                (str "import(process.argv[1]).then(async host=>{const j=await import('data:text/javascript;base64," js64 "');"
                     "const w=await host.instantiateKotoba(Buffer.from(process.argv[2],'base64'));const run=x=>{"
                     "if(x['entry-count']('acc')!==2n||x['entry-field']('acc',0n,'established-date')[2]!=='1872'||x['entry-field']('acc',1n,'established-date')[2]!=='1988')throw Error('dates');"
                     "if(x['topic-count']('acc',1n)!==2n||x['topic']('acc',1n,1n)[2]!=='environment')throw Error('topics');"
                     "if(x['by-topic-id']('acc','safety',0n)[2]!=='acc.responsible-care-overview'||x['topic']('acc',1n,2n)[1]!==false)throw Error('query');};"
                     "run(j.instantiateKotoba({}));run(w.instance.exports);}).catch(e=>{console.error(e);process.exit(99)})")
                (.toString (.toUri (.resolve (compiler-root) "runtime/browser-host.mjs"))) wasm64)]
    (is (zero? (:exit probe)) (str (:out probe) (:err probe)))))
(deftest production-source-authority
  (is (= ["src/association_facts.kotoba"]
         (->> (file-seq (io/file "src")) (filter #(.isFile %)) (map str) sort vec))))
