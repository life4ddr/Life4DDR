const kebabVertical = `<svg data-v-5bf4cb66="" version="1.1" width="3" height="16" viewBox="0 0 3 16" aria-hidden="true" class="octicon octicon-kebab-vertical"><path data-v-5bf4cb66="" fill-rule="evenodd" d="M0 2.5a1.5 1.5 0 1 0 3 0 1.5 1.5 0 0 0-3 0zm0 5a1.5 1.5 0 1 0 3 0 1.5 1.5 0 0 0-3 0zM1.5 14a1.5 1.5 0 1 1 0-3 1.5 1.5 0 0 1 0 3z"></path></svg>`
const clone = `<svg width="20px" aria-hidden="true" focusable="false" data-prefix="fa" data-icon="clone" role="img" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 512 512" class="svg-inline--fa fa-clone fa-w-16"><path fill="currentColor" d="M464 0c26.51 0 48 21.49 48 48v288c0 26.51-21.49 48-48 48H176c-26.51 0-48-21.49-48-48V48c0-26.51 21.49-48 48-48h288M176 416c-44.112 0-80-35.888-80-80V128H48c-26.51 0-48 21.49-48 48v288c0 26.51 21.49 48 48 48h288c26.51 0 48-21.49 48-48v-48H176z" class=""></path></svg>`

module.exports = function ({ line, source, totals }) {
  return {
    date: `${new Date().toISOString().substring(0, 10)}`,
    timestamp: `${new Date().toISOString()}`,
    sourceLink: `[${source.path}:${line}](${source.path}:${line})`,
    dueEmoji: dueEmoji(totals),
    recentEmoji: recentEmoji(totals),
    wipEmoji: wipEmoji(totals),
    cardTotal: cardTotal(totals),
    clone,
    kebabVertical
  }
}

const EMOJI = {
  BAD: ':rotating_light:',
  GREAT: ':rocket:',
  SLEEP: ':sleeping:',
  GOOD: ':2nd_place_medal:',
}

function formatEmoji(emoji) {
  return `<span style="font-size: 1.5em;">${emoji}</span>`
}

function dueEmoji(totals) {
  const due = totals["What's Due?"]
  let emoji = EMOJI.GOOD
  if (due >= 3) {
    emoji = EMOJI.BAD
  } else if (due === 0) {
    emoji = EMOJI.GREAT
  }
  return formatEmoji(emoji)
}

function recentEmoji(totals) {
  const recentlyCompleted = totals['Recently Completed']
  let emoji = EMOJI.GOOD
  if (recentlyCompleted >= 3) {
    emoji = EMOJI.GREAT
  } else if (recentlyCompleted === 0) {
    emoji = EMOJI.BAD
  }
  return formatEmoji(emoji)
}

function wipEmoji(totals) {
  const doing = totals['DOING']
  let emoji = EMOJI.GOOD
  if (doing >= 3) {
    emoji = EMOJI.BAD
  } else if (doing === 0) {
    emoji = EMOJI.SLEEP
  } else if (doing === 1) {
    emoji = EMOJI.GREAT
  }
  return formatEmoji(emoji)
}

function cardTotal(totals) {
  let count = 0
  Object.keys(totals).forEach((list) => {
    count += totals[list]
  })
  return count
}
