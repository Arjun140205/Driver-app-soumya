/**
 * Picks the text to show from a failed HTTP call. The backend answers validation errors as
 * { message, errors: { field: text } } and other errors as { message } (or a plain string).
 */
export function serverMessage(err: any, fallback: string): string {
  const body = err ? err.error : null;
  if (body && typeof body === 'object') {
    if (body.errors && typeof body.errors === 'object') {
      const first = Object.values(body.errors)[0];
      if (typeof first === 'string' && first) {
        return first;
      }
    }
    if (typeof body.message === 'string' && body.message) {
      return body.message;
    }
  }
  if (typeof body === 'string' && body) {
    return body;
  }
  return fallback;
}
