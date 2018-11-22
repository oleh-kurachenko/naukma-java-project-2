# NaUKMA Java Project 2 Breakout

Study project at NaUKMA (National University of Kyiv Mohyla Academy) Java
course. This project was done in the team with another student Viktoria Kozopas.

## Project goal and requirements

The goal was to create graphic Breakout game using graphics from 
[ACM Java Library]. The game was expected to support the number of constants,
such as window height and width, ball speed, number of bricks, etc. The ball
had to start moving in the random direction (by naturally not horizontal)
after the countdown. Game had to support the number of lives.

Also, we were expected to add some our own features, for example to improve
design. Our team decided not to change standard graphics, but to improve
physics behaviour of objects - especially to make ball behave naturally when
touching bricks.

## Implementation

The game starts with countdown:

![Game starts with countdown](readme_resources/screenshot_1.png)

There are a counter for remaining bricks and remaining lives:

![Gameplay picture 1](readme_resources/screenshot_2.png)

![Gameplay picture 2](readme_resources/screenshot_3.png)

When all lives are gone, game is over:

![Game over](readme_resources/screenshot_4.png)

Window and game settings can be configured:

![Game settings are configurable](readme_resources/screenshot_5.png)

When all bricks are hit, you wins:

![You win final](readme_resources/screenshot_6.png)

## ACM Java Library usage

The [ACM Java Library] usage is regulated by
[the license][ACM Java Library License].

[ACM Java Library]: https://cs.stanford.edu/people/eroberts/jtf/
[ACM Java Library License]: https://cs.stanford.edu/people/eroberts/jtf/documents/License.pdf
