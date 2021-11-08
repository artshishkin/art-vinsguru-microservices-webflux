import {Injectable, NgZone} from '@angular/core';
import {Observable} from "rxjs";

import {SseService} from "./sse.service";
import {environment} from "../../environments/environment";
import {Product} from "./product.model";

@Injectable({
  providedIn: 'root'
})
export class ProductService {

  private productsUrl = environment.productsUrl;

  constructor(private zone: NgZone, private sseService: SseService) {
  }

  getProducts(): Observable<Product> {
    return new Observable<Product>(
      (subscriber) => {
        const eventSource = this.sseService.getEventSource(this.productsUrl);

        eventSource.onmessage = event => {
          this.zone.run(() => subscriber.next(JSON.parse(event.data)));
        };

        eventSource.onerror = error => {
          this.zone.run(() => subscriber.error(error));
        };

      });
  }
}
