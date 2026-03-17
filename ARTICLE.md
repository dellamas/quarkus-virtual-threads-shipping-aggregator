# Agregação de cotações de frete com Quarkus e virtual threads

Em um e-commerce, o checkout quase sempre depende de consultas externas para estimar frete. O problema aparece quando o backend precisa chamar vários parceiros ao mesmo tempo e todos eles expõem APIs lentas, baseadas em I/O bloqueante. Se o desenho da aplicação não considerar isso desde o início, a latência cresce rápido e a complexidade do código sobe junto.

Este projeto mostra uma abordagem objetiva com Quarkus 3.32.3 e Java 21: uma API que recebe um pedido de cotação, chama `FASTBOX`, `ECONOSHIP` e `PICKNPACK` em paralelo e devolve o resultado consolidado ordenado por menor preço.

## O problema prático

Num fluxo de compra, a experiência esperada é simples: informar origem, destino, SKU, quantidade e valor do pedido para receber opções de entrega em poucos instantes. Nos bastidores, isso costuma significar:

- três ou mais integrações HTTP externas
- tempos de resposta diferentes por parceiro
- código bloqueante em SDKs ou clientes legados
- necessidade de consolidar e ordenar o resultado final

Com threads tradicionais por requisição, escalar esse modelo pode custar caro. Com virtual threads, dá para manter o estilo imperativo e ainda tratar grande volume de espera por I/O com muito menos atrito.

## A ideia da aplicação

O projeto expõe três endpoints:

- `POST /api/shipping-options/quotes`
- `GET /api/shipping-options/partners`
- `GET /api/shipping-options/diagnostics`

O endpoint principal usa um executor de virtual threads para disparar as chamadas dos parceiros em paralelo. Cada cliente simula I/O bloqueante com `Thread.sleep`, justamente para demonstrar um caso comum de integração que não nasceu reativa.

O diagnóstico complementa a demonstração com três informações úteis:

- total de cotações processadas
- quantidade de chamadas por parceiro
- tempo médio por parceiro

Isso ajuda a visualizar o custo de cada integração e mostra como a agregação se comporta ao longo do uso.

## Por que virtual threads fazem sentido aqui

Nem todo sistema precisa virar reativo para lidar com concorrência. Quando o problema principal é espera por I/O, virtual threads permitem:

- continuar escrevendo código direto e legível
- paralelizar chamadas bloqueantes com baixo overhead
- reduzir a pressão por pools de threads tradicionais
- deixar a regra de negócio mais simples de entender e manter

Neste cenário, isso significa que a classe de serviço pode orquestrar os três parceiros com poucas linhas, sem callbacks, sem combinadores reativos e sem sacrificar clareza.

## Resultado funcional

Para um payload como este:

```json
{
  "origin": "sao-paulo-sp",
  "destination": "belo-horizonte-mg",
  "sku": "SKU-9001",
  "quantity": 3,
  "orderValue": 799.90
}
```

A API retorna as cotações ordenadas do menor para o maior preço. Esse detalhe importa porque aproxima o projeto de um caso real de checkout, em que a comparação imediata entre parceiros faz parte da experiência do cliente.

Além disso, o endpoint de diagnóstico passa a refletir as chamadas executadas, o que ajuda a validar a agregação e observar o comportamento do fluxo após uma cotação real.

## Fechamento

Este projeto não tenta simular um gateway logístico completo. O foco está em mostrar uma estrutura enxuta, publicável e fácil de explicar para demonstrar paralelismo com I/O bloqueante em Quarkus usando virtual threads.

Se quiser explorar a implementação com calma, o código está aqui:
https://github.com/dellamas/quarkus-virtual-threads-shipping-aggregator

Para acompanhar mais conteúdos sobre Java, Quarkus e arquitetura, vale me encontrar no LinkedIn:
https://www.linkedin.com/in/luisfabriciodellamas/

E, se curtir esse tipo de material prático, tem mais publicações em:
https://dev.to/dellamas
