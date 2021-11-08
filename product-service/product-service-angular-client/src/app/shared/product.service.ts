import {Injectable, NgZone} from '@angular/core';
import {Observable} from "rxjs";

import {SseService} from "./sse.service";
import {environment} from "../../environments/environment";
import {Product} from "./product.model";

@Injectable({
  providedIn: 'root'
})
export class ProductService {

  constructor(private zone: NgZone, private sseService: SseService) {
  }

  getProducts(maxPrice?: number): Observable<Product> {
    return new Observable<Product>(
      (subscriber) => {

        let productsUrl = environment.productsUrl;
        if (maxPrice) productsUrl += '?maxPrice=' + maxPrice;
        const eventSource = this.sseService.getEventSource(productsUrl);

        eventSource.onmessage = event => {
          this.zone.run(() => subscriber.next(JSON.parse(event.data)));
        };

        eventSource.onerror = error => {
          this.zone.run(() => subscriber.error(error));
        };

      });
  }
}
