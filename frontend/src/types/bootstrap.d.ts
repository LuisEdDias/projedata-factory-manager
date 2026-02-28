declare module 'bootstrap' {
  export class Modal {
    constructor(element: Element, options?: any)
    show(): void
    hide(): void
    toggle(): void
    dispose(): void
    static getInstance(element: Element): Modal | null
    static getOrCreateInstance(element: Element, options?: any): Modal
  }
}
