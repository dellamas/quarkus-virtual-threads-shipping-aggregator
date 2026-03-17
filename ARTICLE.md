# Agregacao de cotacoes de frete com Quarkus e virtual threads

Em um e-commerce, o checkout quase sempre depende de consultas externas para estimar frete. O problema aparece quando o backend precisa chamar varios parceiros ao mesmo tempo e todos eles expõem APIs lentas, baseadas em I/O bloqueante. Se o desenho da aplicacao nao considerar isso desde o inicio, a latencia cresce rapido e a complexidade do codigo sobe junto.

Este lab mostra uma abordagem objetiva com Quarkus 3.32.3 e Java 21: uma API que recebe um pedido de cotacao, chama `FASTBOX`, `ECONOSHIP` e `PICKNPACK` em paralelo e devolve o resultado consolidado ordenado por menor preco.

## O problema pratico

Num fluxo de compra, a experiencia esperada e simples: informar CEP, peso e valor do pedido e receber opcoes de entrega em poucos instantes. Nos bastidores, isso costuma significar:

- tres ou mais integracoes HTTP externas
- tempos de resposta diferentes por parceiro
- codigo bloqueante em SDKs ou clientes legados
- necessidade de consolidar e ordenar o resultado final

Com threads tradicionais por requisicao, escalar esse modelo pode custar caro. Com virtual threads, da para manter o estilo imperativo e ainda tratar grande volume de espera por I/O com muito menos atrito.

## A ideia do lab

O projeto expoe tres endpoints:

- `POST /api/shipping-options/quotes`
- `GET /api/shipping-options/partners`
- `GET /api/shipping-options/diagnostics`

O endpoint principal usa um executor de virtual threads para disparar as chamadas dos parceiros em paralelo. Cada cliente simula I/O bloqueante com `Thread.sleep`, justamente para demonstrar um caso comum de integracao que nao nasceu reativo.

O diagnostico complementa a demonstracao com tres informacoes uteis:

- total de cotacoes processadas
- quantidade de chamadas por parceiro
- tempo medio por parceiro

Isso ajuda a visualizar o custo de cada integracao e mostra como a agregacao se comporta ao longo do uso.

## Por que virtual threads fazem sentido aqui

Nem todo sistema precisa virar reativo para lidar com concorrencia. Quando o problema principal e espera por I/O, virtual threads permitem:

- continuar escrevendo codigo direto e legivel
- paralelizar chamadas bloqueantes com baixo overhead
- reduzir a pressao por pools de threads tradicionais
- deixar a regra de negocio mais simples de entender e manter

No contexto deste lab, isso significa que a classe de servico pode orquestrar os tres parceiros com poucas linhas, sem callbacks, sem combinadores reativos e sem sacrificar clareza.

## Resultado funcional

Para um payload como este:

```json
{
  "orderId": "order-1001",
  "destinationZipCode": "04538-132",
  "weightKg": 3.75,
  "declaredValue": 899.90,
  "itemsCount": 4
}
```

A API retorna as cotacoes ordenadas do menor para o maior preco. Esse detalhe importa porque aproxima o lab de um caso real de checkout, onde a comparacao imediata entre parceiros faz parte da experiencia do cliente.

## Fechamento

Este projeto nao tenta simular um gateway logistico completo. O foco esta em mostrar uma estrutura enxuta, publicavel e facil de explicar para demonstrar paralelismo com I/O bloqueante em Quarkus usando virtual threads.

Veja o código do lab, adapte para o seu contexto e compartilhe sua versão em: https://github.com/dellamas/quarkus-virtual-threads-shipping-aggregator-lab

Para acompanhar mais conteúdos sobre Java, Quarkus e arquitetura, conecte-se no LinkedIn: https://www.linkedin.com/in/luisfabriciodellamas/

Mais artigos e experimentos em: https://dev.to/dellamas
