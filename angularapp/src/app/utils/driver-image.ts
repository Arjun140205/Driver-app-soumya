/** Generic avatar (white disc, dark silhouette) used when a driver has no photo. */
export const DEFAULT_AVATAR =
  'data:image/svg+xml;utf8,' +
  encodeURIComponent(
    '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100">' +
      '<circle cx="50" cy="50" r="49" fill="#fff" stroke="#111" stroke-width="2"/>' +
      '<circle cx="50" cy="38" r="15" fill="#111"/>' +
      '<path d="M22 80c4-17 16-24 28-24s24 7 28 24c-8 8-18 12-28 12S30 88 22 80z" fill="#111"/>' +
      '</svg>'
  );

/** Returns something usable as an <img src>, accepting full data URLs and raw Base64 strings. */
export function getDriverImage(image?: string | null): string {
  if (!image || !image.trim()) {
    return DEFAULT_AVATAR;
  }
  if (image.startsWith('data:') || image.startsWith('http')) {
    return image;
  }
  return 'data:image/png;base64,' + image;
}

/** (error) handler for <img>: fall back to the default avatar. */
export function useDefaultAvatar(event: Event): void {
  const img = event.target as HTMLImageElement;
  if (img && img.src !== DEFAULT_AVATAR) {
    img.src = DEFAULT_AVATAR;
  }
}
