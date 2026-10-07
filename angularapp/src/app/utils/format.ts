const pad = (n: number): string => (n < 10 ? '0' + n : String(n));

/** Accepts "YYYY-MM-DD", [y, m, d], a Date or null and returns "YYYY-MM-DD" ('' when empty). */
export function toDateString(value: any): string {
  if (value === null || value === undefined || value === '') {
    return '';
  }
  if (Array.isArray(value)) {
    return `${value[0]}-${pad(value[1])}-${pad(value[2])}`;
  }
  if (value instanceof Date) {
    return `${value.getFullYear()}-${pad(value.getMonth() + 1)}-${pad(value.getDate())}`;
  }
  return String(value).substring(0, 10);
}

/** Accepts "HH:mm[:ss]", [h, m, s], a Date or null and returns "HH:mm" ('' when empty). */
export function toTimeString(value: any, withSeconds = false): string {
  if (value === null || value === undefined || value === '') {
    return '';
  }
  let h = 0;
  let m = 0;
  let s = 0;
  if (Array.isArray(value)) {
    h = value[0] || 0;
    m = value[1] || 0;
    s = value[2] || 0;
  } else if (value instanceof Date) {
    h = value.getHours();
    m = value.getMinutes();
    s = value.getSeconds();
  } else {
    const parts = String(value).split(':');
    h = Number(parts[0]) || 0;
    m = Number(parts[1]) || 0;
    s = Number(parts[2]) || 0;
  }
  return withSeconds ? `${pad(h)}:${pad(m)}:${pad(s)}` : `${pad(h)}:${pad(m)}`;
}

/** "YYYY-MM-DD" -> "DD/MM/YYYY" */
export function toDisplayDate(value: any): string {
  const iso = toDateString(value);
  if (!iso) {
    return '';
  }
  const [y, m, d] = iso.split('-');
  return `${d}/${m}/${y}`;
}

export function todayString(): string {
  return toDateString(new Date());
}

/** Combines a trip date and time slot into a Date (local time). */
export function combineDateTime(date: any, time: any): Date | null {
  const d = toDateString(date);
  const t = toTimeString(time, true);
  if (!d || !t) {
    return null;
  }
  const [y, mo, day] = d.split('-').map(Number);
  const [h, mi, s] = t.split(':').map(Number);
  return new Date(y, mo - 1, day, h, mi, s);
}

/** Parses strings such as "3 hours", "1.5 hrs", "45 minutes" into minutes (0 when unknown). */
export function parseDurationToMinutes(text: string | null | undefined): number {
  if (!text) {
    return 0;
  }
  const lower = text.toLowerCase();
  const match = lower.match(/(\d+(\.\d+)?)/);
  if (!match) {
    return 0;
  }
  const value = parseFloat(match[1]);
  return /min/.test(lower) ? Math.round(value) : Math.round(value * 60);
}

export function formatDuration(totalMinutes: number): string {
  const hours = Math.floor(totalMinutes / 60);
  const minutes = totalMinutes % 60;
  return `${hours} hours ${minutes} minutes`;
}
